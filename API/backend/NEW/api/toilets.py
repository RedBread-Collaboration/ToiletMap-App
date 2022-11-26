from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from AddressParser import getAddressByCoords
from db import get_db
from models.toilet import Toilet

toilets_router = APIRouter(prefix='/toilets', tags=['toilets'])


@toilets_router.post("/addPoint")
def addPoint(lat: float,
             lon: float,
             title: str,
             desc: str,
             db: Session = Depends(get_db)):
    toilet_model = Toilet()
    toilet_model.lat = lat
    toilet_model.lon = lon
    toilet_model.title = title
    toilet_model.desc = desc
    toilet_model.address = getAddressByCoords(lat, lon)

    db.add(toilet_model)
    db.commit()
    return getPointById(toilet_model.id, db)


@toilets_router.get("/getAllPoints")
def getAllPoints(db: Session = Depends(get_db)):
    return db.query(Toilet).all()


@toilets_router.get("/getPointById")
def getPointById(toilet_id: int, db: Session = Depends(get_db)):
    toilet_model = db.query(Toilet).filter(Toilet.id == toilet_id).first()

    if toilet_model is None:
        raise HTTPException(
            status_code=404,
            detail=f"Toilet with ID {toilet_id} is not exist"
        )

    return toilet_model


@toilets_router.get("/getPointByAddress")
def getPointByAddress(address: int, db: Session = Depends(get_db)):
    toilet_model = db.query(Toilet).filter(Toilet.address == address).first()

    if toilet_model is None:
        raise HTTPException(    
            status_code=404,
            detail=f"Toilet with Address {address} is not exist"
        )

    return toilet_model


@toilets_router.get("/getPointByCoords")
def getPointByCoords(lat: float, lon: float, db: Session = Depends(get_db)):
    toilet_model = db.query(Toilet).filter(
        Toilet.lat == lat and Toilet.lon == lon
    ).first()

    if toilet_model is None:
        raise HTTPException(
            status_code=404,
            detail=f"Toilet with Coords {lat}, {lon} is not exist"
        )

    return toilet_model


@toilets_router.put("/updatePoint")
def updatePoint(toilet_id: int,
                lat: float,
                lon: float,
                title: str,
                desc: str,
                db: Session = Depends(get_db)):
    toilet_model = db.query(Toilet).filter(Toilet.id == toilet_id).first()

    if toilet_model is None:
        raise HTTPException(
            status_code=404,
            detail=f"Toilet with ID {toilet_id} is not exist"
        )

    toilet_model.lat = lat
    toilet_model.lon = lon
    toilet_model.title = title
    toilet_model.desc = desc
    toilet_model.address = getAddressByCoords(lat, lon)

    db.add(toilet_model)
    db.commit()
    return getPointById(toilet_model.id, db)


@toilets_router.delete("/removePointById")
def removePointById(toilet_id: int, db: Session = Depends(get_db)):
    toilet_model = db.query(Toilet).filter(Toilet.id == toilet_id).first()

    if toilet_model is None:
        raise HTTPException(
            status_code=404,
            detail=f"Toilet with ID {toilet_id} is not exist"
        )

    db.query(Toilet).filter(Toilet.id == toilet_id).delete()
    db.commit()
