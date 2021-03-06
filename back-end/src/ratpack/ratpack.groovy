import archangeldlt.webapi.GethProxyHandler
import archangeldlt.webapi.UploadHandler
import archangeldlt.webapi.PreservicaImportHandler

import static ratpack.groovy.Groovy.ratpack
import org.slf4j.LoggerFactory

def findPrefix() {
  def p = System.getProperty('handler.prefix')
  if (!p)
    p = System.getenv('HANDLER_PREFIX')
  return p ? "${p}/" : ""
} // findPrefix

def pathPrefix = findPrefix()
def monitor_path = "${pathPrefix}monitor"
def frontend_path = "${pathPrefix}front-end"
def geth_path = "${pathPrefix}geth"
def upload_path = "${frontend_path}/upload"
def preservica_path = "${frontend_path}/import-preservica"

def oneMeg = 1048576
def tenMegs = oneMeg * 10
def twentyFiveMegs = oneMeg * 25
def uploadLimit = twentyFiveMegs

ratpack {
  serverConfig { conf ->
    conf.maxContentLength(uploadLimit)

    def logger = LoggerFactory.getLogger("RatpackServer")
    logger.info(pathPrefix ? "Path prefix ${pathPrefix}" : "No path prefix")
    logger.info("Available endpoints")
    [monitor_path, frontend_path, upload_path, preservica_path].forEach { logger.info("  /${it}") }
  }

  handlers {
    get(monitor_path) {
      redirect("/${monitor_path}/index.html")
    }
    get(pathPrefix) {
      redirect("/${frontend_path}/index.html")
    }

    prefix(monitor_path) {
      files {
        dir "monitor" indexFiles "index.html"
      }
    }
    prefix(frontend_path) {
      files {
        dir "front-end" indexFiles "index.html"
      }
    }

    post(upload_path, new UploadHandler())
    post(geth_path, new GethProxyHandler())
    post(preservica_path, new PreservicaImportHandler())
  }
}
