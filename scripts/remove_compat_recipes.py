import os

dataDir = "../src/generated/resources/data/"
mods = {"silents_mechanisms", "immersiveengineering", "mekanism", "eidolon", "mysticalworld", "thermal", "iceandfire"}

# searches src/generated/resources/data/ for all files containing a mod name and moves them to src/generated/resources/compat_recipes
def remove():
    for fileName in os.listdir(dataDir):
        for modName in mods:
            if fileName.endswith(modName + ".json"):
                os.rename(dataDir + fileName, "../src/generated/resources/compat_recipes/" + fileName)
                break
