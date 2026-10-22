import base64,hashlib,json,pathlib,subprocess
p=pathlib.Path('.migration/gitlab-20261022')
m=json.loads((p/'manifest.json').read_text())
data=base64.b64decode(b''.join((p/'history.bundle.b64').read_bytes().split()),validate=True)
assert hashlib.sha256(data).hexdigest()==m['bundleSha256'],'Backup checksum mismatch'
bundle=pathlib.Path('/tmp/total-music-gitlab.bundle');bundle.write_bytes(data)
def git(*args):return subprocess.check_output(['git',*args],text=True).strip()
remote={line.split('\t')[1]:line.split('\t')[0] for line in git('ls-remote','origin').splitlines()}
for ref,sha in m['expectedGithubRefs'].items():
 assert remote.get(ref)==sha,'GitHub changed during audit: '+ref
for ref in m['createOnlyRefs']:
 subprocess.run(['git','check-ref-format',ref],check=True)
 assert ref not in remote,'Target already exists; refusing to overwrite: '+ref
subprocess.run(['git','bundle','verify',str(bundle)],check=True)
subprocess.run(['git','fetch','--no-tags',str(bundle),'+refs/remotes/gitlab-export/*:refs/remotes/gitlab-restore/*'],check=True)
for ref,sha in m['createOnlyRefs'].items():
 git('cat-file','-e',sha)
subprocess.run(['git','fsck','--full'],check=True)
specs=[sha+':'+ref for ref,sha in m['createOnlyRefs'].items()]
# Atomic, non-forced push: only new archive refs and the integration branch.
subprocess.run(['git','push','--atomic','origin',*specs],check=True)
after={line.split('\t')[1]:line.split('\t')[0] for line in git('ls-remote','origin').splitlines()}
assert all(after.get(ref)==sha for ref,sha in m['expectedGithubRefs'].items())
assert all(after.get(ref)==sha for ref,sha in m['createOnlyRefs'].items())
pathlib.Path('migration-result.json').write_text(json.dumps({'verified':True,'candidateCommit':m['candidateCommit'],'createdRefs':m['createOnlyRefs'],'originalGithubRefsUnchanged':True,'bundleSha256':m['bundleSha256']},indent=2)+'\n')
print('Verified migration: original GitHub refs unchanged; imported GitLab history retained.')
