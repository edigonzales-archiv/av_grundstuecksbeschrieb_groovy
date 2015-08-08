import java.util.logging.Logger

import geoscript.layer.Layer
import geoscript.workspace.PostGIS
import geoscript.feature.Field


Logger logger = Logger.getLogger("")
logger.info ("I am a test info log")

// Connect to a PostGIS database
def pg = new PostGIS('xanadu2','localhost','5432','av_avdpool_ch','mspublic','mspublic')

// Print the format
println("Format: $pg.format")

// Print the layer names
//println("Layers:")

//pg.layers.each{lyr -> println(lyr)}


//def gemgre = pg.get("gemeindegrenzen_gemeindegrenze")

//println gemgre

// für jede liegenschaft nochmals eine view


def sqlLiegenschaften = '''\
SELECT g.tid, g.nummer, g.nbident, l.gem_bfs, l.geometrie
FROM av_avdpool_ch.liegenschaften_grundstueck as g, av_avdpool_ch.liegenschaften_liegenschaft as l
WHERE g.tid = l.liegenschaft_von
AND g.gem_bfs = %bfsnr%
LIMIT 1
'''

def sqlGrundstuecksbeschrieb = '''\

'''

def primaryKeyFields = ['tid']
def params = [['bfsnr', '9999']] // null does not work
def options = [primaryKeyFields:primaryKeyFields, 'params':params]

def lyr = pg.createView(options, "liegen", sqlLiegenschaften, new Field("geometrie", "Polygon", "EPSG:21781"))

lyr.getFeatures(['params':['bfsnr': '2601']]).each { feat ->

  println feat

  return

}
