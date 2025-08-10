package simulations

object Camt052Generator {

  // Pre-compiled template for better performance
  private val xmlTemplate = """<?xml version="1.0" encoding="UTF-8"?>
<Document xmlns="urn:iso:std:iso:20022:tech:xsd:camt.052.001.08">
  <BkToCstmrAcctRpt>
    <GrpHdr>
      <MsgId>%s</MsgId>
      <CreDtTm>%s</CreDtTm>
      <NbOfTxs>1</NbOfTxs>
      <MsgRcpt>
        <Nm>Customer Name</Nm>
        <Id>
          <OrgId>
            <Othr>
              <Id>%s</Id>
            </Othr>
          </OrgId>
        </Id>
      </MsgRcpt>
    </GrpHdr>
    <Rpt>
      <Id>RPT-%s</Id>
      <ElctrncSeqNb>1</ElctrncSeqNb>
      <CreDtTm>%s</CreDtTm>
      <FrToDt>
        <FrDtTm>%s</FrDtTm>
        <ToDtTm>%s</ToDtTm>
      </FrToDt>
      <Acct>
        <Id>
          <IBAN>GB33BUKB20201%s</IBAN>
        </Id>
        <Svcr>
          <FinInstnId>
            <BICFI>%s</BICFI>
          </FinInstnId>
        </Svcr>
      </Acct>
      <Bal>
        <Tp>
          <CdOrPrtry>
            <Cd>OPBD</Cd>
          </CdOrPrtry>
        </Tp>
        <Amt Ccy="%s">%s</Amt>
        <CdtDbtInd>CRDT</CdtDbtInd>
        <Dt>
          <Dt>%s</Dt>
        </Dt>
      </Bal>
      <Ntry>
        <Amt Ccy="%s">%s</Amt>
        <CdtDbtInd>CRDT</CdtDbtInd>
        <Sts>BOOK</Sts>
        <BookgDt>
          <Dt>%s</Dt>
        </BookgDt>
        <ValDt>
          <Dt>%s</Dt>
        </ValDt>
      </Ntry>
    </Rpt>
  </BkToCstmrAcctRpt>
</Document>"""

  def generate(uniqueId: String, messageId: String, timestamp: String,
                         amount: String, currency: String, accountId: String, bic: String): String = {
    val dateOnly = timestamp.substring(0, 10)
    
    xmlTemplate.format(
      messageId, timestamp, accountId, uniqueId, timestamp, timestamp, timestamp,
      accountId, bic, currency, amount, dateOnly, currency, amount, dateOnly, dateOnly
    )
  }
}
