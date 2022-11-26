from sqlalchemy import Column, Integer, Float, String

from db import Base


class Toilet(Base):
    __tablename__ = "toilets"

    id = Column(Integer, primary_key=True, autoincrement=True)
    lat = Column(Float, nullable=False)
    lon = Column(Float, nullable=False)
    title = Column(String, nullable=False)
    desc = Column(String, nullable=False)
    address = Column(String, nullable=False)
