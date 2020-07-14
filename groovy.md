# Groovy primers for FLIMJ analysis

## Example of a minimal code to run fit in ImageJ-Groovy kernel after loading an img file XYT dimensions

"""code groovy
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
rldRslt = op.run("flim.fitGlobal", param)
"""

## Other fitting routines

- flim.fitLMA
- flim.fitBayes
- flim.fitGlobal
- flim.fitPhasor
- flim.fitRLD

## Using an rectangle ROI [10:25,10:25,T]

import net.imglib2.roi.geom.real.OpenWritableBox
min = [ 10, 10 ]
max = [ 25, 25 ]
roi = new OpenWritableBox([ min[0] - 1, min[1] - 1 ] as double[], [ max[0] + 1, max[1] + 1 ] as double[])

## Adding restrain parameter

import flimlib.FLIMLib
FLIMLib.GCI_set_restrain_limits(3, [1, 1, 1] as int[], [0, 0, 0] as float[], [1e4f,  1e4f, 6f] as float[])

import flimlib.RestrainType
param.restrain = RestrainType.ECF_RESTRAIN_USER



