[![](https://travis-ci.com/flimlib/flimj-ops.svg?branch=master)](https://travis-ci.com/flimlib/flimj-ops)

# flimj-ops

FLIMJ Ops are a collection of FLIM analysis ops based on [FLIMLib](https://github.com/flimlib/flimlib). It extends the single-transient fitting functions in FLIMLib to dataset-level fitting ops. Currently supported fitting ops include: RLD, MLA, Global, Phasor, and single-component Bayesian.

Besides curve fitting, FLIMJ Ops also provide a variety of pre-processing options such as pixel binning, intensity thresholding, ROI masking as well as post-processing utility ops for e.g. calculating τ<sub>m</sub> (mean lifetime), A<sub>i</sub>% (fractional contribution) and pseudocoloring the result with LUT.

Most of the FLIMJ fitting ops supports multithreading. Fitting a dataset like the one below takes no more than a couple of seconds!

# Example usage
Open [test2.sdt](test_files/test2.sdt) in [Fiji](https://fiji.github.io/). Execute in [Script Editor](http://imagej.github.io/Using_the_Script_Editor) as Groovy:

```groovy
# @ImageJ ij
# @ImgPlus img

op = ij.op()

// set up parameters
import flimlib.flimj.FitParams

param = new FitParams()
param.transMap = img; // input 3-dimensional (x, y, t) dataset
param.xInc= 0.040     // time difference between bins (ns)
param.ltAxis = 2      // time bins lay along axis #2

// op call
fittedImg = op.run("flim.fitLMA", param).paramMap

// display each parameter
zImg = op.transform().hyperSliceView(fittedImg, param.ltAxis, 0)
AImg = op.transform().hyperSliceView(fittedImg, param.ltAxis, 1)
tauImg = op.transform().hyperSliceView(fittedImg, param.ltAxis, 2)

ij.ui().show("z", zImg)
ij.ui().show("A", AImg)
ij.ui().show("tau", tauImg)

```

Output (z in [-1, 1], A in [0, 4], tau in[0, 3]):

![example output](images/example%20z.png)![example output](images/example%20A.png)![example output](images/example%20tau.png)

See more examples in [Demo.ipynb](notebooks/Demo.ipynb) and [groovy.md](groovy.md).

# Using from a Java project

To depend on FLIMJ Ops, copy the following to your `pom.xml`:

```xml
  <properties>
    <flimj-ops.version>2.1.1</flimj-ops.version>
  </properties>

  <dependencies>
    <!-- FLIMJ Ops dependency -->
    <dependency>
      <groupId>flimlib</groupId>
      <artifactId>flimj-ops</artifactId>
      <version>${flimj-ops.version}</version>
    </dependency>
  </dependencies>
```

# See also

 - [FLIMLib](https://github.com/flimlib/flimlib): Curve fitting library for FLIM
   - [Debug tutorial](https://github.com/flimlib/flimlib/wiki/Debugging)
 - [FLIMJ UI](https://github.com/flimlib/flimj-ui): ImageJ plugin built on top of FLIMJ Ops.

# Citation

Comming soon...
