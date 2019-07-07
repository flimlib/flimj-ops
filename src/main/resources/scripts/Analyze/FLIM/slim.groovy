//@ OpService ops
//@ Dataset data
//@ String(choices={"MLA", "Global"}, style="listBox") fitType
//@ Integer(label="Number of components", min=1) nComp
//@ Integer(label="Binning radius", min=0) rKnl
//@ Float(label="Time bin resolution", min=0) xInc
//@ File(label="Instrument response", style="open", required=false) irfFile
// output net.imagej.slim.FitResults out

import net.imagej.slim.FitParams
import net.imagej.slim.SlimOps
import ij.gui.GenericDialog

// setup
params = new FitParams()
params.transMap = data.getImgPlus()
params.nComp = nComp
params.xInc = xInc
for (i in 0..(data.numDimensions() - 1)) {
//	println(data.axis(i).type().toString())
	if (data.axis(i).type().toString().equals("Time")) {
		params.ltAxis = i
		break;
	}
}

if (irfFile != null) {
	def pts = []
	// read lines and get the second number
    irfFile.withReader { reader ->
    	def line
        while ((line = reader.readLine()) != null) {
        	pts.add(line.split("\\s")[1] as Integer)
        }
    }
    // trim and normalize
    def iStart = 0, iEnd = 1
	def sum = 0
    pts.eachWithIndex { pt, idx ->
    	if (pt == 0) {
    		if (idx == iStart) {
    			iStart++
    		}
    	}
    	else {
    		iEnd = idx + 1
    	}
    	sum += pt
    }
    if (iEnd < iStart) {
    	iEnd = iStart
    }
    params.instr = pts.subList(iStart, iEnd).collect { it / sum }
}

knl = rKnl > 0 ? SlimOps.makeSquareKernel(2 * rKnl + 1) : null

// cup of tea
def thread = Thread.start {
	gui = new GenericDialog("Processing...")
	gui.addMessage("Fitting in progress")
	gui.hideCancelButton()
	gui.showDialog()
	gui.dispose()
}

out = ops.run("slim.fit" + fitType, params, null, knl)

// done
thread.interrupt()

out.paramMap
