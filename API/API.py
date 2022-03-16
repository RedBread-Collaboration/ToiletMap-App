from configparser import ConfigParser, NoSectionError
from klein import run, route
from ToiletDB import ToiletDB
from PointsParser import YaMap
import json
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
    data = req.content.read()
    if data:
        data = json.loads(data)
        if data['token'] == token:
            Succeed(req)
            return str(db.getAllPoints())
    return Forbidden(req)


@route('/getPointById/<int:id>', branch=True)
def getPointById(req, id=0):
    data = req.content.read()
    if req.content:
        data = json.loads(data)
        if data['token'] == token:
            Succeed(req)
            return str(db.getPointById(id))
    return Forbidden(req)


@route('/getPointByAddress/<string:address>', branch=True)
def getPointByAddress(req, address=""):
    data = req.content.read()
    if req.content:
        data = json.loads(data)
        if data['token'] == token:
            Succeed(req)
            return str(db.getPointByAddress(address))
    return Forbidden(req)


@route('/addPoint', methods=['POST'], branch=True)
def addPoint(req):
    data = req.content.read()
    if req.content:
        data = json.loads(data)
        if data['token'] == token:
            
            if db.getPointByAddress(data['address']):
                return BadRequest(req)
            
            if data['address']:
                lat, lon = yaMap.getCoordsByAddress(data['address'])
                
            toilet = Toilet(
                lat=lat,
                lon=lon,
                title=data['title'],
                address=data['address'],
                desc=data['desc']
            )
            Succeed(req)
            return str(db.addPoint(toilet))
    return Forbidden(req)


@route('/removePointById/<int:id>', branch=True)
def removePointById(req, id=0):
    data = req.content.read()
    if req.content:
        data = json.loads(data)
        if data['token'] == token:
            db.removePointById(id)
            Succeed(req)
            return req.redirect('/')
    return Forbidden(req)


@route('/')
def index(req):
    return req.redirect('/getAllPoints')


# infosakh.ru/wc/
print(f"http://{server}:{port}")
run(server, port)
