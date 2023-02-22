function AlltrackThirdPartySharing(isEnabled) {
    this.isEnabled = isEnabled;
    this.granularOptions = [];
    this.partnerSharingSettings = [];
}

AlltrackThirdPartySharing.prototype.addGranularOption = function(partnerName, key, value) {
    this.granularOptions.push(partnerName);
    this.granularOptions.push(key);
    this.granularOptions.push(value);
};

AlltrackThirdPartySharing.prototype.addPartnerSharingSetting = function(partnerName, key, value) {
    this.partnerSharingSettings.push(partnerName);
    this.partnerSharingSettings.push(key);
    this.partnerSharingSettings.push(value);
};
