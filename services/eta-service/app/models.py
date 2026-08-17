from datetime import datetime
from typing import Optional
from uuid import UUID

from pydantic import BaseModel


class LocationUpdate(BaseModel):
    shipmentId: UUID
    latitude: float
    longitude: float
    location: str
    timestamp: datetime


class EtaResponse(BaseModel):
    shipmentId: UUID
    currentLocation: Optional[str] = None
    estimatedArrival: Optional[datetime] = None
    remainingDistanceKm: Optional[float] = None
    averageSpeedKmh: Optional[float] = None
    delayMinutes: float = 0