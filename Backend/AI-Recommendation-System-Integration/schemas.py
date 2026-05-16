from pydantic import BaseModel


class RouteSchema(BaseModel):
    next_action: str
