# Challenge 1 - Launch
Your challenge is simple: get to space. This project contains a 2-dimensional spaceflight simulation, and it's your job to build a guidance computer that can take a rocket from liftoff to a stable orbit.

Some approximations have been taken regarding orbital mechanics to simplify the mathematics, but generally your goal is this: reach a stable altitude of at least 100km, travelling with a horizontal speed of at least 7.84 km/s, with as little vertical speed as possible. You accomplish this by lifting off vertically, then slowing tilting towards the left or right to pick up horizontal speed while coasting to your final altitude. Be careful though! In the denser parts of the atmosphere, turbulence and air resistance can have a large impact on your rocket's trajectory, so you'll need to be able to correct for these things.

## Controls
There are some primitive user controls that allow you to interact with the simulation:

- `SPACE` - Start the launch.
- `ESC` - Abort the launch.
- `A` - Activate right-side RCS thruster to rotate left.
- `D` - Activate left-side RCS thruster to rotate right.

You are free to change/add/remove keybindings as necessary, but the guidance computer should be able to take the rocket from the ground to orbit without any human input.
