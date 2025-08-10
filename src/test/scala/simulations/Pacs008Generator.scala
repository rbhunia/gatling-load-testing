package simulations

object Pacs008Generator {
  
  // Pre-compile template for better performance
  private val xmlTemplate = """<?xml version="1.0" encoding="UTF-8"?>
<Document xmlns="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.08">
  <FIToFICstmrCdtTrf>
    <GrpHdr>
      <MsgId>%s</MsgId>
      <CreDtTm>%s</CreDtTm>
      <NbOfTxs>1</NbOfTxs>
      <SttlmInf>
        <SttlmMtd>CLRG</SttlmMtd>
      </SttlmInf>
      <InstgAgt>
        <FinInstnId>
          <BICFI>%s</BICFI>
        </FinInstnId>
      </InstgAgt>
      <InstdAgt>
        <FinInstnId>
          <BICFI>NWBKGB2L</BICFI>
        </FinInstnId>
      </InstdAgt>
    </GrpHdr>
    <CdtTrfTxInf>
      <PmtId>
        <InstrId>%s</InstrId>
        <EndToEndId>%s</EndToEndId>
        <TxId>TXN-%s</TxId>
      </PmtId>
      <IntrBkSttlmAmt Ccy="%s">%s</IntrBkSttlmAmt>
      <AccptncDtTm>%s</AccptncDtTm>
      <InstgAgt>
        <FinInstnId>
          <BICFI>%s</BICFI>
        </FinInstnId>
      </InstgAgt>
      <InstdAgt>
        <FinInstnId>
          <BICFI>NWBKGB2L</BICFI>
        </FinInstnId>
      </InstdAgt>
      <Dbtr>
        <Nm>Debtor Name %s</Nm>
        <PstlAdr>
          <Ctry>GB</Ctry>
        </PstlAdr>
      </Dbtr>
      <DbtrAcct>
        <Id>
          <IBAN>GB33BUKB20201%s</IBAN>
        </Id>
      </DbtrAcct>
      <DbtrAgt>
        <FinInstnId>
          <BICFI>%s</BICFI>
        </FinInstnId>
      </DbtrAgt>
      <Cdtr>
        <Nm>Creditor Name %s</Nm>
        <PstlAdr>
          <Ctry>GB</Ctry>
        </PstlAdr>
      </Cdtr>
      <CdtrAcct>
        <Id>
          <IBAN>GB29NWBK60161%s</IBAN>
        </Id>
      </CdtrAcct>
      <CdtrAgt>
        <FinInstnId>
          <BICFI>NWBKGB2L</BICFI>
        </FinInstnId>
      </CdtrAgt>
    </CdtTrfTxInf>
  </FIToFICstmrCdtTrf>
</Document>"""

  def generate(uniqueId: String, messageId: String, timestamp: String,
               amount: String, currency: String, accountId: String, bic: String,
               endToEndId: String, instructionId: String): String = {
    
    // Use String.format for better performance than string interpolation
    val creditorAccountSuffix = DataPool.getRandomAccountId.substring(3) // Reuse from pool
    
    xmlTemplate.format(
      messageId, timestamp, bic, instructionId, endToEndId, uniqueId,
      currency, amount, timestamp, bic, uniqueId, accountId, bic,
      uniqueId, creditorAccountSuffix
    )
  }

}
