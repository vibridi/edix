# edix

This project is a Java8 implementation of an EDI parser. It was born as a fork of [berryworks/edireader](https://github.com/BerryWorksSoftware/edireader), then went through a few rewriting cycles and ended up in its own repository.

Existing off-the-shelf EDI/X12 libraries might suffer from one or more of the following issues:
- they aren't open source or modifiable
- they aren't free (as in free beer)
- they don't do conversion to/from the format you need right now

EDIX attempts to solve these issues by being open, free and by introducing a small DOM-like EDI interchange model that allows arbitrary format conversions.

Old master Miao Tsu said: "Software must be closed for modification but open for extension". 

EDIX especially wants to be open for extension, so that you can transform X12 into your cool proprietary format, if you want, without having to write a parser from scratch.

## Development status

EDIX is currently a work in progress, whose development has the secondary goal to acquire know-how about EDI standards. It is by no means a finished product, but hopefully it will be.

TODOs:
- TA1 acks
- ISB/ISE segments support
- BDS parsing
- manage LE segments


## How do I use this anyway?

For now, don't. It isn't ready. Though you can clone the repository and do whatever you normally do with cloned repositories. Just be aware that the code is being worked on. There aren't many Javadocs, package and class names might be messy or obsolete and functionality missing. 
The best source of information about components and functionalities right now is the unit tests.
