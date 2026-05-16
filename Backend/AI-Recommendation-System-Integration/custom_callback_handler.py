from typing import Any
from langchain_community.callbacks import StreamlitCallbackHandler as make_streamlit_callback


class CustomStreamlitCallbackHandler:
    def __init__(self, parent_container: Any):
        self._parent_container = parent_container
        self._handler = make_streamlit_callback(parent_container)

    def __getattr__(self, name: str) -> Any:
        return getattr(self._handler, name)

    def write_agent_name(self, name: str):
        self._parent_container.write(name)
