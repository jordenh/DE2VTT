from cx_Freeze import setup, Executable

setup(
    name = "Middleman",
    version = "1.0",
    description = "A simple tcp to serial middle man",
    executables = [Executable("middleman.py")]
)
