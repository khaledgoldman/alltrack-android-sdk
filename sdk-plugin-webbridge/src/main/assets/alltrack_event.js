function AlltrackEvent(eventToken) {
    this.eventToken = eventToken;
    this.revenue = null;
    this.currency = null;
    this.callbackParameters = [];
    this.partnerParameters = [];
    this.orderId = null;
    this.callbackId = null;
}

AlltrackEvent.prototype.setRevenue = function(revenue, currency) {
    this.revenue = revenue;
    this.currency = currency;
};

AlltrackEvent.prototype.addCallbackParameter = function(key, value) {
    this.callbackParameters.push(key);
    this.callbackParameters.push(value);
};

AlltrackEvent.prototype.addPartnerParameter = function(key, value) {
    this.partnerParameters.push(key);
    this.partnerParameters.push(value);
};

AlltrackEvent.prototype.setOrderId = function(orderId) {
    this.orderId = orderId;
};

AlltrackEvent.prototype.setCallbackId = function(callbackId) {
    this.callbackId = callbackId;
};
