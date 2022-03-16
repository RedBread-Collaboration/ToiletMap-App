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
        toilets = cur.execute(f"SELECT * FROM {self.tablename}").fetchall()
        return toilets
        
    def getPointById(self, id:int) -> Toilet:
        cur = self.connect()
        toilet = cur.execute(f"SELECT * FROM {self.tablename} WHERE id={id}").fetchone()
        return toilet
        
    def getPointByAddress(self, address:str) -> Toilet:
        cur = self.connect()
        toilet = cur.execute(f"SELECT * FROM {self.tablename} WHERE address='{address}'").fetchone()
        return toilet
    
    def addPoint(self, toilet: Toilet) -> Toilet:
        print(toilet.getTitle())
        cur = self.connect()
        cur.execute(f"""INSERT INTO {self.tablename} (
                {self.t_lat},
                {self.t_lon},
                {self.t_title},
                {self.t_desc},
                {self.t_address}
            ) VALUES (
                '{toilet.getCoords()[0]}',
                '{toilet.getCoords()[1]}',
                '{toilet.getTitle()}',
                '{toilet.getDesc()}',
                '{toilet.getAddress()}'
            )""")
        self.save()
        return self.getPointByAddress(toilet.getAddress())
    
    def removePointById(self, id:int):
        cur = self.connect()
        cur.execute(f"DELETE FROM {self.tablename} WHERE id={id}")
        self.save()