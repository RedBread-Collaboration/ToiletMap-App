from configparser import ConfigParser, NoSectionError
from klein import run, route
from ToiletDB import ToiletDB
from PointsParser import YaMap
from urllib.parse import unquote
# from Toilet import Toilet
from ResponseCodes import *
from datetime import datetime as dt


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
now_date = dt.strftime(dt.now(), "%Y-%m-%d %H-%M-%S")
logFile = open(f'./logs/{now_date}.log', 'w')

# logFile = FileHandler("./latest.log", 'a', "utf-8")
# logging.basicConfig(filename=("./latest.log"))
# logging.basicConfig(filename=("keylog.txt"), level=logging.DEBUG, format=" %(asctime)s - %(message)s")

TAG = "ToiletMap"


@route('/getAllPoints/', branch=True)
def getAllPoints(req):
    key = getKey('key')
    if key == token:
        toiletList = db.getAllPoints()

        SetContentTypeJSON(req)
        Succeed(req)
        return str(toiletList)
    return Forbidden(req)


@route('/getPointById/', branch=True)
def getPointById(req):
    key = getKey(req)
    if key == token:
        id = req.getHeader('id')
        toilet = db.getPointById(id)

        SetContentTypeJSON(req)
        Succeed(req)
        return str(toilet)
    return Forbidden(req)


@route('/getPointByAddress/', branch=True)
def getPointByAddress(req):
    key = getKey(req)
    if key == token:
        address = req.getHeader('address')
        toilet = db.getPointByAddress(address)

        SetContentTypeJSON(req)
        Succeed(req)
        return str(toilet)
    return Forbidden(req)


@route('/getPointByCoords/', branch=True)
def getPointByCoords(req):
    key = getKey(req)
    if key == token:
        lat = req.getHeader('lat')
        lon = req.getHeader('lon')
        toilet = db.getPointByCoords(lat, lon)

        SetContentTypeJSON(req)
        Succeed(req)
        return str(toilet)
    return Forbidden(req)


@route('/getCityByCoords/', branch=True)
def getCityByCoords(req):
    key = getKey(req)
    if key == token:
        lat = req.getHeader('lat')
        lon = req.getHeader('lon')
        city = yaMap.getCityByCoords(lat, lon).split(', ')[2]

        SetContentTypeJSON(req)
        Succeed(req)
        return city
    return Forbidden(req)


@route('/addPoint/', methods=['POST'], branch=True)
def addPoint(req):
    key = key = getKey(req)
    if key == token:
        title = unquote(req.getHeader('title'))
        # address = unquote(req.getHeader('address'))
        lat = float(req.getHeader('lat'))
        lon = float(req.getHeader('lon'))
        desc = unquote(req.getHeader('desc'))

        address = yaMap.getAddressByCoords(lat, lon)
        # if db.getPointByAddress(address):
        #     return BadRequest(req)

        # if address:
        #     lat, lon = yaMap.getCoordsByAddress(address)
        #     address = yaMap.getAddressByCoords(lat, lon)

        toilet = db.addPoint(
            lat=lat,
            lon=lon,
            title=title,
            address=address,
            desc=desc
        )
        SetContentTypeJSON(req)
        Created(req)
        return str(toilet)
    return Forbidden(req)


@route('/updatePoint/', methods=['PUT'], branch=True)
def updatePoint(req):
    key = getKey(req)
    if key == token:
        id = int(req.getHeader('id'))
        title = unquote(req.getHeader('title'))
        # address = unquote(req.getHeader('address'))
        lat = float(req.getHeader('lat'))
        lon = float(req.getHeader('lon'))
        desc = unquote(req.getHeader('desc'))
        address = yaMap.getAddressByCoords(lat, lon)

        if not db.getPointByAddress(address):
            return BadRequest(req)

        # if address:
        #     lat, lon = yaMap.getCoordsByAddress(address)
        #     address = yaMap.getAddressByCoords(lat, lon)

        toilet = db.updatePoint(
            id=id,
            lat=lat,
            lon=lon,
            title=title,
            address=address,
            desc=desc,
        )
        SetContentTypeJSON(req)
        Succeed(req)
        return str(toilet)
    return Forbidden(req)


@route('/removePointById/', methods=['DELETE'], branch=True)
def removePointById(req):
    key = getKey(req)
    if key == token:
        id = int(req.getHeader('id'))
        db.removePointById(id)
        return NoContent(req)
    return Forbidden(req)


# infosakh.ru/services/wc/
print(f"http://{server}:{port}")
print(f"Started logging to {now_date}.log file...")
run(server, port, logFile)
