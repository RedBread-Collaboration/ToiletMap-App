from configparser import ConfigParser, NoSectionError
from klein import run, route
from ToiletDB import ToiletDB
from PointsParser import YaMap
from urllib.parse import unquote
from Toilet import Toilet
from ResponseCodes import *

def print_error(tag, msg):
    color_red = '\033[91m'
    color_none = '\033[0m'
    print(color_red + f"{tag}: {msg}" + color_none)
    exit(1)


config = ConfigParser()
config.read("./apiConf.ini")

try:
    server = config.get('API', 'server')
    # print(server)
    port = config.get('API', 'port')
    # print(port)
    token = config.get('API', 'token')
    # print(token)
    map_token = config.get('API', 'map_token')
    # print(token)
except NoSectionError:
    conf_file = open("./apiConf.ini", 'w')
    config.add_section('API')
    config.set('API', 'server', '')
    config.set('API', 'port', '')
    config.set('API', 'token', '')
    config.set('API', 'map_token', '')
    config.write(conf_file)
    conf_file.close()
    print_error("Config Error", "apiConf.ini file not found")
    
if not token:
    print_error("API Config Error", "field 'token' is empty")
    
if not map_token:
    print_error("API Config Error", "field 'map_token' is empty")


db = ToiletDB()
yaMap = YaMap(map_token)


@route('/getAllPoints/', branch=True)
def getAllPoints(req):
    key = req.getHeader('key')
    if key == token:
        toiletList = db.getAllPoints()
        
        SetResponseJSON(req)
        Succeed(req)
        return str(toiletList)
    return Forbidden(req)


@route('/getPointById/', branch=True)
def getPointById(req):
    key = getKey(req)
    if key == token:
        id = req.getHeader('id')
        toilet = db.getPointById(id)
        
        SetResponseJSON(req)
        Succeed(req)
        return str(toilet)
    return Forbidden(req)


@route('/getPointByAddress/', branch=True)
def getPointByAddress(req):
    key = getKey(req)
    if key == token:
        address = req.getHeader('address')
        toilet = db.getPointByAddress(address)
        
        SetResponseJSON(req)
        Succeed(req)
        return str(toilet)
    return Forbidden(req)


@route('/addPoint/', methods=['POST'], branch=True)
def addPoint(req):
    key = key = getKey(req)
    if key == token:
        title = unquote(req.getHeader('title'))
        address = unquote(req.getHeader('address'))
        desc = unquote(req.getHeader('desc'))
        
        if db.getPointByAddress(address):
            return BadRequest(req)
        
        if address:
            lat, lon = yaMap.getCoordsByAddress(address)
            
        toilet = db.addPoint(
            lat=lat,
            lon=lon,
            title=title,
            address=address,
            desc=desc
        )
        print(toilet)
        SetResponseJSON(req)
        Succeed(req)
        return str(toilet)
    return Forbidden(req)


@route('/removePointById/', branch=True)
def removePointById(req):
    key = getKey(req)
    if key == token:
        id = req.getHeader('id')
        db.removePointById(id)
        Succeed(req)
        return req.redirect('/')
    return Forbidden(req)


@route('/')
def index(req):
    return req.redirect('/getAllPoints/')


# infosakh.ru/wc/
print(f"http://{server}:{port}")
run(server, port)
