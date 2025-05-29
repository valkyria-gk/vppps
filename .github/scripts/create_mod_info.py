import json
import os
from pathlib import Path
import sys


def get_required_environment(key: str) -> str:
    result = os.getenv(key)
    if not result:
        raise RuntimeError(
            f"Environment variable {key} is not defined, it must be defined!"
        )
    return result


if __name__ == "__main__":
    args = sys.argv[1:]
    mod_info_template_path = Path(args[0]).resolve()
    output_path = Path(args[1]).resolve()

    mod_version = get_required_environment("MOD_VERSION")

    if not mod_info_template_path.exists():
        raise RuntimeError(f"{mod_info_template_path} does not exist.")

    with open(mod_info_template_path, "r", encoding="utf-8") as f:
        mod_info_object = json.loads(f.read())

    mod_info_object["id"] = "vppps"
    mod_info_object["version"] = mod_version

    with open(output_path, "w", encoding="utf-8") as f:
        f.write(json.dumps(mod_info_object, indent=1))

    sys.exit(0)
