class Toilet:
    
    def __init__(self, id:int, lat:float, lon:float, title:str, address:str, desc:str=""):
        self.id = id
        self.lat = lat
        self.lon = lon
        self.title = title
        self.desc = desc
        self.address = address
        
    def getCoords(self) -> tuple:
        return (self.lat, self.lon)
    
    def setCoords(self, lat, lon):
        self.lat = lat
        self.lon = lon
    
    def getTitle(self) -> str:
        return self.title
    
    def setTitle(self, title):
        self.title = title
        
    def getAddress(self) -> str:
        return self.address
    
    def setAddress(self, address):
        self.address = address
        
    def getDesc(self) -> str:
        return self.desc
    
    def setDesc(self, desc):
        self.desc = desc
        
    def toJSON(self) -> dict:
        toilet = {
            'id': self.id,
            'lat': self.lat,
            'lon': self.lon,
            'title': self.title,
            'address': self.address,
            'desc': self.desc
        }
        return toilet