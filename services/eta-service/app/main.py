from fastapi import FastAPI, HTTPException
from uuid import UUID

from app.storage import eta_results

app = FastAPI(
    title="ETA Service",
    version="1.0.0"
)


@app.get("/actuator/health")
def health():
    return {
        "status": "UP",
        "service": "eta-service"
    }


@app.get("/api/v1/eta/{shipment_id}")
def get_eta(shipment_id: UUID):

    eta = eta_results.get(shipment_id)

    if eta is None:
        raise HTTPException(
            status_code=404,
            detail=f"ETA not found for shipment: {shipment_id}"
        )

    return eta