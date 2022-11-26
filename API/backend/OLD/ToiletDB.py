import sqlite3
from Toilet import Toilet

class ToiletDB:
    
    def connect(self):
        self.conn = sqlite3.connect("./DB/Toilets.db")
        return self.conn.cursor()
        
    def close(self):
        self.conn.close()
    
    def save(self):
        self.conn.commit()
        self.close()
    
    def __init__(self):
        self.tablename = "ToiletPoints"
        self.t_id = "id"
        self.t_lat = "lat"
        self.t_lon = "lon"
        self.t_title = "title"
        self.t_desc = "desc"
        self.t_address = "address"
        
        cur = self.connect()
        cur.execute(f"""CREATE TABLE IF NOT EXISTS {self.tablename} (
            {self.t_id} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            {self.t_lat} REAL NOT NULL,
            {self.t_lon} REAL NOT NULL,
            {self.t_title} TEXT NOT NULL,
            {self.t_address} TEXT NOT NULL,
            {self.t_desc} TEXT
        )""")
        self.save()
        
    def getAllPoints(self) -> list:
        cur = self.connect()
        toiletList = cur.execute(f"SELECT * FROM {self.tablename}").fetchall()
        for i in range(len(toiletList)):
            toiletList[i] = Toilet(*toiletList[i]).toJSON()
        return toiletList
        
    def getPointById(self, id:int) -> Toilet:
        cur = self.connect()
        toilet = cur.execute(f"SELECT * FROM {self.tablename} WHERE id={id}").fetchone()
        return Toilet(*toilet).toJSON()
        
    def getPointByAddress(self, address:str):
        cur = self.connect()
        toilet = cur.execute(f"SELECT * FROM {self.tablename} WHERE address='{address}'").fetchone()
        if toilet:
            return Toilet(*toilet).toJSON()
        
    def getPointByCoords(self, lat:float, lon:float):
        cur = self.connect()
        toilet = cur.execute(f"SELECT * FROM {self.tablename} WHERE lat={lat} AND lon={lon}").fetchone()
        if toilet:
            return Toilet(*toilet).toJSON()
    
    def addPoint(self, lat:float, lon:float, title:str, address:str, desc:str=""):
        # print(title)
        cur = self.connect()
        cur.execute(f"""INSERT INTO {self.tablename} (
                {self.t_lat},
                {self.t_lon},
                {self.t_title},
                {self.t_address},
                {self.t_desc}
            ) VALUES (
                {lat},
                {lon},
                '{title}',
                '{address}',
                '{desc}'
            )""")
        self.save()
        return self.getPointByAddress(address)
    
    def updatePoint(self, id:int, lat:float, lon:float, title:str, address:str, desc:str=""):
        cur = self.connect()
        cur.execute(f"""UPDATE {self.tablename} SET (
                {self.t_lat},
                {self.t_lon},
                {self.t_title},
                {self.t_address},
                {self.t_desc}
            ) = (
                {lat},
                {lon},
                '{title}',
                '{address}',
                '{desc}'
            ) WHERE id={id}""")
        self.save()
        return self.getPointById(id)
    
    def removePointById(self, id:int):
        cur = self.connect()
        cur.execute(f"DELETE FROM {self.tablename} WHERE id={id}")
        self.save()