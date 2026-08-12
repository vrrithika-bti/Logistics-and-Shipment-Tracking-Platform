import streamlit as st


st.set_page_config(
    page_title="Logistics Tracking Platform",
    page_icon="🚚",
    layout="wide"
)


st.title("🚚 Logistics & Shipment Tracking Platform")

st.write(
    "Real-time logistics and shipment tracking dashboard"
)

st.divider()

col1, col2, col3 = st.columns(3)

with col1:
    st.metric(
        "Total Shipments",
        "0"
    )

with col2:
    st.metric(
        "In Transit",
        "0"
    )

with col3:
    st.metric(
        "Delivered",
        "0"
    )

st.info(
    "Shipment Service connection will be added next."
)