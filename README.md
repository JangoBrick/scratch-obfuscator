# Scratch/BYOB Obfuscator

[![Build Status](https://travis-ci.org/meyfa/scratch-obfuscator.svg?branch=master)](https://travis-ci.org/meyfa/scratch-obfuscator)

This is a fun project for obfuscating Scratch/BYOB projects using
[scratchlib](https://github.com/meyfa/scratchlib).

## Contributing

### Cloning

Since this is using scratchlib as a submodule, cloning needs to be done
recursively. The submodule may also not be edited directly.

```
git clone --recursive https://github.com/meyfa/scratch-obfuscator.git
```

### Building

This project should be built using the Maven `package` goal. That goal will
generate `target/scratch-obfuscator-<version>-jar-with-dependencies.jar`,
which is the final build result. Nevertheless, running from within Eclipse is
supported as well.
