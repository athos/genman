# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]

## [v0.2.0] - 2017-04-05
### Added
- Add `merge-groups` and `extend-group` to provide ways to create an adhoc generator group from existing ones
- Add `def-gen-group` to name adhoc generator groups
- Make `->overrides-map` public which enables to convert a generator group as a overrides map

### Changed
- Rename the `ToGenGroup` protocol to `ToOverridesMap` and move its definition to a separate namespace

## v0.1.0 - 2018-03-18
### Added
- Basic switching mechanism between multiple generator implementanios for a single spec
- Generator group merging

[Unreleased]: https://github.com/athos/genman/compare/v0.1.0...HEAD
[v0.2.0]: https://github.com/athos/genman/compare/v0.1.0...v0.2.0
