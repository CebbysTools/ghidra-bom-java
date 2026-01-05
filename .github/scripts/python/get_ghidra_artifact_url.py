import requests as Requests
import argparse as Argparse
import typing as Typing
import sys as System
import os as Os


PARSER = Argparse.ArgumentParser(
    description="Get the Ghidra download URL by artifact version."
)
PARSER.add_argument(
    name_or_flags="--version",
    required=True,
    help="Ghidra version (e.g., 12.0)"
)
PARSER.add_argument(
    name_or_flags="--token",
    required=False,
    help="GitHub Personal Access Token"
)

class Arguments:
    version: str
    token: str|None

def main():
    arguments = get_arguments()
    
def get_arguments() -> Arguments:
    return get_raw_arguments()

def get_raw_arguments() -> Typing.Any:
    return PARSER.parse_args()

if __name__ == "__main__":
    main()