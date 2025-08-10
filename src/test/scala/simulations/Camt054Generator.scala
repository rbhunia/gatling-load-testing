package simulations

object Camt054Generator {

  // Pre-compiled template for better performance
  private val xmlTemplate = """<?xml version="1.0" encoding="UTF-8"?>
<Document xmlns="urn:iso:std:iso:20022:tech:xsd:camt.054.001.08">
  <BkToCstmrDbtCdtNtfctn>
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
    <Ntfctn>
      <Id>NOTIF-%s</Id>
      <ElctrncSeqNb>1</ElctrncSeqNb>
      <CreDtTm>%s</CreDtTm>
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
        <NtryDtls>
          <TxDtls>
            <Refs>
              <MsgId>%s</MsgId>
              <AcctSvcrRef>REF-%s</AcctSvcrRef>
            </Refs>
          </TxDtls>
        </NtryDtls>
      </Ntry>
    </Ntfctn>
  </BkToCstmrDbtCdtNtfctn>
</Document>"""

  def generate(uniqueId: String, messageId: String, timestamp: String,
                         amount: String, currency: String, accountId: String, bic: String): String = {
    val dateOnly = timestamp.substring(0, 10)
    
    xmlTemplate.format(
      messageId, timestamp, accountId, uniqueId, timestamp, accountId, 
      bic, currency, amount, dateOnly, dateOnly, messageId, uniqueId
    )
  }
}