<@s.url id="orcIDRemoveURL" action="orcidRemove" namespace="/user/secure" />
<@s.url action="authors" namespace="/static" includeParams="none" id="authorsURL"/>
<div class="panel orcid-info short cf">
    <div class="left">
        <a href="http://orcid.org/about/what-is-orcid" target="_blank" class="image-text orcid"
           alt="Find out more about ORCID on their website">ORCID</a>

    </div>
    <div class="right ">
    <#-- Change orcURL to be production -->
        <p>Your ORCID account: <strong>
            <a href="http://orcid.org/${orcid}" target="_blank" title="Your linked ORCID account"
               alt="Your ORCID Profile">${orcid}</a>
        </strong><br/>
            is linked to your PLOS account. <strong>
                <a href="${orcIDRemoveURL}"
                   data-js="orcid-delink" title="Remove the link to your ORCID account"
                   alt="Find out more about ORCID on their website">De-link</a>
            </strong>
        </p>
    </div>
</div>

<div class="orcid-form no-display">
    <p class="messaging action"><strong>Are you sure you want to de-link your ORCID account from your PLOS
        account?</strong></p>

    <p class="messaging success no-display"><strong>Your account has been successfully de-linked</strong></p>

    <p class="messaging failure no-display"><strong>Something has gone wrong! Please try again later.</strong></p>
</div>

