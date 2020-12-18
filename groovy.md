# Groovy primers for FLIMJ analysis

## Example of a minimal code to run fit in ImageJ-Groovy kernel after loading an img file XYT dimensions

```groovy
# @ImageJ ij
# @ImgPlus img

op = ij.op()

import flimlib.flimj.FitParams
param = new FitParams()
param.transMap = img;

param.xInc= 0.040 // ns
param.ltAxis = 2
param.nComp = 2

import flimlib.flimj.FlimOps
rldRslt = op.run("flim.fitRLD", param)
rldRslt.paramMap
```

## Other fitting routines

- flim.fitLMA
- flim.fitBayes
- flim.fitGlobal
- flim.fitPhasor
- flim.fitRLD

## Other useful snippets for FLIM Analysis

### Using an rectangle ROI [10:25,10:25,T]
```groovy
import net.imglib2.roi.geom.real.OpenWritableBox
min = [ 10, 10 ]
max = [ 25, 25 ]
roi = new OpenWritableBox([ min[0] - 1, min[1] - 1 ] as double[], [ max[0] + 1, max[1] + 1 ] as double[])
result = op.run("flim.fitRLD", param,FlimOps.SQUARE_KERNEL_3,roi)
result.paramMap
```
### Adding restrain to parameter (z: [0, 1e2], A: [0, 1e4], tau: [0, 6])
```groovy
import flimlib.FLIMLib
import flimlib.RestrainType

param.restrain = RestrainType.ECF_RESTRAIN_USER
FLIMLib.GCI_set_restrain_limits([true, true, true] as boolean[], [0, 0, 0] as float[], [1e2f,  1e4f, 6f] as float[])

```
### Using parameter estimator
```groovy
import flimlib.flimj.ParamEstimator
est = new ParamEstimator(param)
est.estimateStartEnd()
```

### Using user defined fit range 

This range start/stop is sensitive to the peak of decay curve especially without an IRF

```groovy
param.fitStart = 55
param.fitEnd = 255
```

### Adding spatial bin sizes

```groovy
import flimlib.flimj.FlimOps
rldRslt = op.run("flim.fitLMA", param,roi,FlimOps.SQUARE_KERNEL_3)
```
### Console print for debugging

```groovy
println("Estimated start, end: " + param.fitStart + ", " + param.fitEnd)
println("time-resolution: " + param.xInc)
```
## Fitting single curve 

```groovy
# @ImageJ ij
# @ImgPlus img
op = ij.op()

import net.imglib2.img.array.ArrayImgs 

array = [1, 1, 1, 64, 32, 16, 8, 4, 2, 1] as float[]
img = ArrayImgs.floats(array, 1, 1, array.length)

import flimlib.flimj.FitParams
param = new FitParams()
param.transMap = img;
param.xInc = 1
param.ltAxis = 2
param.nComp = 1

import flimlib.flimj.ParamEstimator
est = new ParamEstimator(param)
est.estimateStartEnd()
println("Estimated start, end: " + param.fitStart + ", " + param.fitEnd)

paramMap = op.run("flim.fitLMA", param).paramMap

println(paramMap)
println("z: " + paramMap[0])
println("A: " + paramMap[1])
println("tau: " + paramMap[2])

```
