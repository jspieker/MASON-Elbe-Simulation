[![build status](https://gitlab.uni-oldenburg.de/elbe-simulation/Simulation/badges/master/build.svg)](https://gitlab.uni-oldenburg.de/elbe-simulation/Simulation/commits/master) [![coverage report](https://gitlab.uni-oldenburg.de/elbe-simulation/Simulation/badges/master/coverage.svg)](https://gitlab.uni-oldenburg.de/elbe-simulation/Simulation/commits/master)

## Elbe-Simulation
The fabulous Elbe simulation with multiple options to change the behavior of ships within the elbe. Weka is included in this simulation as a powerful evaluation and data analyzing tool. The Elbe-Simulation uses MASON and its general structure as well as its auxiliary methods to easily enable a multi-agent simulation with the ships.

## Installation guide
```bash
git clone https://gitlab.uni-oldenburg.de/elbe-simulation/Simulation.git
cd Simulation
mvn clean install
java -jar target/elbe-1.0.jar 
```
In case of any errors follow the stacktrace or check you maven or java installation.

## Commit conventions
Please use the [Karma commit message conventions](http://karma-runner.github.io/0.10/dev/git-commit-msg.html) in order to simplify navigating through the repository.

For example, adding new production code should be labelled as `feat: add elbe visualization in mason`, whereas a bugfix or a simple style change should use the `fix` or `style` tag respectively.

## Developer notes
Please write in english (commit messages also).