from yandex_geocoder import Client as YaMap
from config import map_token

# class YaMap(Client):

#     def __init__(self, token):
#         super().__init__(token)


yaMap = YaMap(map_token)


def getCoordsByAddress(address: str) -> tuple:
    lon, lat = yaMap.coordinates(address)
    # print(lon, lat)
    return (float(lat), float(lon))


def getAddressByCoords(lat: float, lon: float) -> str:
    return yaMap.address(latitude=lat, longitude=lon)


# def getCityByCoords(lat: float, lon: float):
#     city = yaMap.address(latitude=lat, longitude=lon)
#     # print(city)
#     return city

# print(getAddressByCoords(lat=59.993536, lon=30.357344))
