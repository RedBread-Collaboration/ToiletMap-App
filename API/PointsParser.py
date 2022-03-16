from yandex_geocoder import Client


class YaMap(Client):
    
    def __init__(self, token):
        super().__init__(token)
        
    def getCoordsByAddress(self, address:str) -> tuple:
        lon, lat = self.coordinates(address)
        print(lon, lat)
        return (float(lat), float(lon))
    
    def getAddressByCoords(self, lat:float, lon:float) -> str:
        return self.address(longitude=lon, latitude=lat)


# mp = YaMap()
# coords = mp.getCoordsByAddress("Южно-Сахалинск Горького 25")
# print(coords)
# addr = mp.getAddressByCoords(lat=coords[0], lon=coords[1])
# print(addr)