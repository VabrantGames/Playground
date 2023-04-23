# Application Tests Guide

To test the playground in a liftoff or libgdx application, create
a folder named `liftoff` or / and `libgdx`. Then create the application
in the newly created folder. The <i>applicationtests</i> project will
look for those folders and create gradle tasks to create a playground with
a project named `bob`, with <i>lwjgl3</i> launcher. A run task is also
generated. 

<h2><u>Tasks created</u></h2>

<h3>CreatePlayground</h3>
e.g `liftoff_createPlayground` <br>
e.g `libgdx_createPlayground`

<h3>Run Playground Project</h3>
e.g `liftoff_runPlaygroundProjectLwjgl3` <br>
e.g `libgdx_runPlaygroundProjectLwjgl3`

These tasks will show up as gradle tasks in the ide for easier 
execution. (Only tested with idea)

 