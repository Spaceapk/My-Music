"""Recover the existing signing identity from a one-run RSA-encrypted envelope."""
import base64, json, os, subprocess, sys, time, zipfile
from pathlib import Path
ROOT = Path(os.environ["SPACE_RECEIVER_DIR"])
def gh(path):
    result = subprocess.run(["gh", "api", path], capture_output=True, text=True)
    return result.stdout if result.returncode == 0 else None
def mask(value):
    escaped = str(value).replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")
    print("::add-mask::" + escaped, flush=True)
try:
    name = ".signing/receiver-" + os.environ["GITHUB_RUN_ID"] + ".json"
    endpoint = "repos/" + os.environ["GITHUB_REPOSITORY"] + "/contents/" + name + "?ref=" + os.environ["GITHUB_REF_NAME"]
    envelope = None
    for attempt in range(180):
        raw = gh(endpoint)
        if raw:
            record = json.loads(raw)
            envelope = json.loads(base64.b64decode(record["content"]))
            break
        time.sleep(5)
    if envelope is None or envelope.get("algorithm") != "RSA-OAEP-SHA256":
        raise RuntimeError("Private envelope not received")
    pieces = []
    for chunk in envelope["chunks"]:
        result = subprocess.run(["openssl", "pkeyutl", "-decrypt", "-inkey", str(ROOT / "recipient.pem"), "-pkeyopt", "rsa_padding_mode:oaep", "-pkeyopt", "rsa_oaep_md:sha256", "-pkeyopt", "rsa_mgf1_md:sha256"], input=base64.b64decode(chunk, validate=True), capture_output=True)
        if result.returncode:
            raise RuntimeError("Private envelope could not be decrypted")
        pieces.append(result.stdout)
    payload = json.loads(b"".join(pieces))
    transfer = payload["transfer"]
    if transfer.get("library_file_id") != "libfile_1230029884a881919873d040c0c2e33c":
        raise RuntimeError("Unexpected signing recovery identity")
    mask(transfer["download_url"])
    for value in (transfer.get("headers") or {}).values():
        mask(value)
    helpers = ROOT / "helpers"; helpers.mkdir(mode=0o700)
    for filename, content in payload["helpers"].items():
        if filename not in {"library_file_transfer.py", "library_hosted_apps.py", "library_upload.py"}:
            raise RuntimeError("Unexpected transfer helper")
        (helpers / filename).write_text(content)
    archive = ROOT / "recovery.zip"
    subprocess.run([sys.executable, str(helpers / "library_file_transfer.py"), "materialize", str(archive)], input=json.dumps(transfer), text=True, check=True)
    with zipfile.ZipFile(archive) as zipped:
        key = zipped.read("space-release.p12")
        password = zipped.read("password.txt").decode().strip()
    mask(password)
    keystore = ROOT / "release.p12"; keystore.write_bytes(key); keystore.chmod(0o600)
    environment = os.environ.copy(); environment["SPACE_SIGN_PASSWORD"] = password
    signer = Path(environment["ANDROID_HOME"]) / "build-tools/36.0.0/apksigner"
    apps = list(Path("built").glob("**/prod/release/*universal*unsigned.apk"))
    tests = list(Path("built").glob("**/androidTest/prod/release/*.apk"))
    if len(apps) != 1 or len(tests) != 1:
        raise RuntimeError("Verified build inputs are ambiguous")
    Path("dist").mkdir(exist_ok=True)
    for source, destination in [(apps[0], "dist/Space-Music-1.0.1-beta.apk"), (tests[0], "dist/test-runner.apk")]:
        subprocess.run([str(signer), "sign", "--v1-signing-enabled", "false", "--v4-signing-enabled", "false", "--ks", str(keystore), "--ks-key-alias", "space-music", "--ks-pass", "env:SPACE_SIGN_PASSWORD", "--key-pass", "env:SPACE_SIGN_PASSWORD", "--out", destination, str(source)], env=environment, check=True)
        verified = subprocess.run([str(signer), "verify", "--verbose", "--print-certs", destination], capture_output=True, text=True, check=True)
        if "5436f4210119058a215c0b0660dead0a9b1bed315f13d6f801645c71c942c5ce" not in verified.stdout:
            raise RuntimeError("Signing certificate does not match the published release")
        Path(destination + ".signature.txt").write_text(verified.stdout)
    subprocess.run([str(Path(environment["ANDROID_HOME"]) / "build-tools/36.0.0/zipalign"), "-c", "-P", "16", "4", "dist/Space-Music-1.0.1-beta.apk"], check=True)
    print("Existing release certificate reused; private recovery material stays ephemeral.")
except Exception:
    print("Private signing recovery failed; no recovery values are disclosed.", file=sys.stderr)
    raise SystemExit(1)
