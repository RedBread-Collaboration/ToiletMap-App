import uvicorn
from fastapi import FastAPI

from api.toilets import toilets_router
from db import Base, engine

app = FastAPI()

app.include_router(toilets_router)

Base.metadata.create_all(bind=engine)

if __name__ == "__main__":
    uvicorn.run(app)
