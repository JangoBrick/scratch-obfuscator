# Scratch/BYOB Obfuscator

This is a fun project for obfuscating Scratch/BYOB projects using
[scratchlib](https://github.com/JangoBrick/scratchlib).

## Contributing

### Cloning

Since this is using scratchlib as a submodule, cloning needs to be done
recursively. The submodule may also not be edited directly.

```
git clone --recursive https://github.com/JangoBrick/scratch-obfuscator.git
```

### Building

This project should be built using the Maven `package` goal. That goal will
generate `target/scratch-obfuscator-<version>-jar-with-dependencies.jar`,
which is the final build result. Nevertheless, running from within Eclipse is
supported as well.
