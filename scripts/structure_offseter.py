# TODO: Find a way to offset a structure without scripts or hardcoding the structure

# pip install NBT
from nbt import nbt

def apply_offset(path: str, offset: int):
    nbtfile = nbt.NBTFile(path, 'rb')
    for block in nbtfile["blocks"]:
        block["pos"][1].value += offset
    nbtfile.write_file(path.replace(".nbt", "_offset.nbt"))

def main():
    path = input("Path to structure: ")
    offset = int(input("Y Offset: "))
    apply_offset(path, offset)

if __name__ == "__main__":
    main()