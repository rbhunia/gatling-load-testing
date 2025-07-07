package simulations

object Camt052Generator {

  def generate(uniqueId: String, messageId: String, timestamp: String,
                         amount: String, currency: String, accountId: String, bic: String): String = {
    s"""<?xml version="1.0" encoding="UTF-8"?>
<Document xmlns="urn:iso:std:iso:20022:tech:xsd:camt.052.001.08">
  <BkToCstmrAcctRpt>
    <GrpHdr>
      <MsgId>$messageId</MsgId>
      <CreDtTm>$timestamp</CreDtTm>
      <NbOfTxs>1</NbOfTxs>
      <MsgRcpt>
        <Nm>Customer Name</Nm>
        <Id>
          <OrgId>
            <Othr>
              <Id>$accountId</Id>
            </Othr>
          </OrgId>
        </Id>
      </MsgRcpt>
    </GrpHdr>
    <Rpt>
      <Id>RPT-$uniqueId</Id>
      <ElctrncSeqNb>1</ElctrncSeqNb>
      <CreDtTm>$timestamp</CreDtTm>
      <FrToDt>
        <FrDtTm>$timestamp</FrDtTm>
        <ToDtTm>$timestamp</ToDtTm>
      </FrToDt>
      <Acct>
        <Id>
          <IBAN>GB33BUKB20201$accountId</IBAN>
        </Id>
        <Svcr>
          <FinInstnId>
            <BICFI>$bic</BICFI>
          </FinInstnId>
        </Svcr>
      </Acct>
      <Bal>
        <Tp>
          <CdOrPrtry>
            <Cd>OPBD</Cd>
          </CdOrPrtry>
        </Tp>
        <Amt Ccy="$currency">$amount</Amt>
        <CdtDbtInd>CRDT</CdtDbtInd>
        <Dt>
          <Dt>${timestamp.substring(0, 10)}</Dt>
        </Dt>
      </Bal>
      <Ntry>
        <Amt Ccy="$currency">$amount</Amt>
        <CdtDbtInd>CRDT</CdtDbtInd>
        <Sts>BOOK</Sts>
        <BookgDt>
          <Dt>${timestamp.substring(0, 10)}</Dt>
        </BookgDt>
        <ValDt>
          <Dt>${timestamp.substring(0, 10)}</Dt>
        </ValDt>
      </Ntry>
    </Rpt>
  </BkToCstmrAcctRpt>
</Document>"""
  }
}
