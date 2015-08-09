import java.util.logging.Logger

import geoscript.layer.Layer
import geoscript.workspace.PostGIS
import geoscript.feature.Field


Logger logger = Logger.getLogger("")
logger.info ("I am a test info log")

def pg = new PostGIS('xanadu2','localhost','5432','av_avdpool_ch','mspublic','mspublic')

def sqlLiegenschaften = '''\
SELECT l.tid, g.nummer, g.nbident, l.gem_bfs, l.geometrie, ST_Area(l.geometrie) as techn_flaeche, l.flaechenmass
FROM av_avdpool_ch.liegenschaften_grundstueck as g, av_avdpool_ch.liegenschaften_liegenschaft as l
WHERE g.gem_bfs = l.gem_bfs
AND g.tid = l.liegenschaft_von
ORDER BY g.gem_bfs, g.nbident, g.nummer
--LIMIT 2
'''

def sqlGrundstuecksbeschrieb = '''\
SELECT art_txt,round(sum(ST_Area(ST_Intersection(a.geometrie, b.geometrie)))::numeric,0) as flaeche
FROM
(
  SELECT l.tid, g.nummer, g.nbident, l.gem_bfs, l.geometrie
  FROM av_avdpool_ch.liegenschaften_grundstueck as g, av_avdpool_ch.liegenschaften_liegenschaft as l
  WHERE g.gem_bfs = l.gem_bfs
  AND g.tid = l.liegenschaft_von
  AND l.tid = '%tid%'
) as a, av_avdpool_ch.bodenbedeckung_boflaeche b
WHERE ST_Intersects(a.geometrie, b.geometrie)
GROUP BY art_txt
ORDER BY art_txt
'''

//def primaryKeyFields = ['tid']
def params = [['tid', '0']] // null does not work
//def options = [primaryKeyFields:primaryKeyFields, 'params':params]
def options = ['params':params]


def lyrLiegenschaften = pg.createView("liegen", sqlLiegenschaften, new Field("geometrie", "Polygon", "EPSG:21781"))
def lyrGrundstuecksbeschrieb = pg.createView(options, "gsbeschrieb", sqlGrundstuecksbeschrieb, new Field("geometrie", "Polygon", "EPSG:21781")) // even w/o any geometry this is needed

println lyrLiegenschaften.count()

def i = 1
lyrLiegenschaften.getFeatures().each { feat ->
    def tid = feat.tid
    def flaechenmass = feat.flaechenmass
    def techn_flaeche = feat.techn_flaeche


    //lyrGrundstuecksbeschrieb.getFeatures(['params':['tid': tid]]).each {
    //  println it
    //}

    def sum_flaeche = lyrGrundstuecksbeschrieb.getFeatures(['params':['tid': tid]]).flaeche.sum()

    println "***************************"
    println i++
    println tid
    println flaechenmass
    println techn_flaeche
    
    println sum_flaeche
    println techn_flaeche - sum_flaeche

}
