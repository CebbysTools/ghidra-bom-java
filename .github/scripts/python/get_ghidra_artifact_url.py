import requests as Requests
import argparse as Argparse
import typing as Typing


PARSER = Argparse.ArgumentParser(
    description="Get the Ghidra download URL by artifact version."
)
PARSER.add_argument(
    "--version",
    required=True,
    help="Ghidra version (e.g., 12.0)"
)
PARSER.add_argument(
    "--token",
    required=False,
    help="GitHub Personal Access Token"
)

class Arguments:
    version: str
    token: str|None

def main():
    arguments = get_arguments()
    print(get_url(arguments.version, arguments.token))
    
def get_url(version:str, token:str|None) -> str:
    # out = ""
    api_url = "https://api.github.com/repos/NationalSecurityAgency/ghidra/releases"
    
    headers = {"Accept": "application/vnd.github.v3+json"}
    if token:
        headers["Authorization"] = f"token {token}"
    
    try:
        response = Requests.get(api_url, headers=headers)
        response.raise_for_status()
        releases: list[dict[str, Typing.Any]] = response.json()
        
        # Tag pattern used by NSA: Ghidra_12.0_build
        target_tag = f"Ghidra_{version}_build"
        
        for release in releases:
            if release.get('tag_name') == target_tag:
                for asset in release.get('assets', []):
                    name: str = asset.get('name', '')
                    if name.startswith(f"ghidra_{version}_PUBLIC_") and name.endswith(".zip"):
                        return asset['browser_download_url']
        raise BaseException(f"Version '{version}' not found")
    except Exception as e:
        raise BaseException(f"Failed to find artifact by version '{version}'") from e

    
def get_arguments() -> Arguments:
    return get_raw_arguments()


def get_raw_arguments() -> Typing.Any:
    return PARSER.parse_args()


if __name__ == "__main__":
    main()