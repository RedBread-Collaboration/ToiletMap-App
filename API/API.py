from configparser import ConfigParser
from klein import run, route
from ToiletDB import ToiletDB
from PointsParser import YaMap
import json
from Toilet import Toilet
from ResponseCodes import *


config = ConfigParser()
config.read("./apiConf.ini")

# server = "localhost"
# port = "8080"
# token = "ba661e842cfe7b9dce1a5153c6e80d5e"

server = config.get('API', 'server')
# print(server)
port = config.get('API', 'port')
# print(port)
token = config.get('API', 'token')
# print(token)
map_token = config.get('API', 'map_token')
# print(token)

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
