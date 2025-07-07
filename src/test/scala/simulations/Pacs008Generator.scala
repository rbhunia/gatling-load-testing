package simulations

import scala.util.Random

object Pacs008Generator {

  def generate(uniqueId: String, messageId: String, timestamp: String,
               amount: String, currency: String, accountId: String, bic: String,
               endToEndId: String, instructionId: String): String = {
    s"""<?xml version="1.0" encoding="UTF-8"?>
<Document xmlns="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.08">
  <FIToFICstmrCdtTrf>
    <GrpHdr>
      <MsgId>$messageId</MsgId>
      <CreDtTm>$timestamp</CreDtTm>
      <NbOfTxs>1</NbOfTxs>
      <SttlmInf>
        <SttlmMtd>CLRG</SttlmMtd>
      </SttlmInf>
      <InstgAgt>
        <FinInstnId>
          <BICFI>$bic</BICFI>
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
        <InstrId>$instructionId</InstrId>
        <EndToEndId>$endToEndId</EndToEndId>
        <TxId>TXN-$uniqueId</TxId>
      </PmtId>
      <IntrBkSttlmAmt Ccy="$currency">$amount</IntrBkSttlmAmt>
      <AccptncDtTm>$timestamp</AccptncDtTm>
      <InstgAgt>
        <FinInstnId>
          <BICFI>$bic</BICFI>
        </FinInstnId>
      </InstgAgt>
      <InstdAgt>
        <FinInstnId>
          <BICFI>NWBKGB2L</BICFI>
        </FinInstnId>
      </InstdAgt>
      <Dbtr>
        <Nm>Debtor Name $uniqueId</Nm>
        <PstlAdr>
          <Ctry>GB</Ctry>
        </PstlAdr>
      </Dbtr>
      <DbtrAcct>
        <Id>
          <IBAN>GB33BUKB20201$accountId</IBAN>
        </Id>
      </DbtrAcct>
      <DbtrAgt>
        <FinInstnId>
          <BICFI>$bic</BICFI>
        </FinInstnId>
      </DbtrAgt>
      <Cdtr>
        <Nm>Creditor Name $uniqueId</Nm>
        <PstlAdr>
          <Ctry>GB</Ctry>
        </PstlAdr>
      </Cdtr>
      <CdtrAcct>
        <Id>
          <IBAN>GB29NWBK60161${Random.nextInt(999999)}</IBAN>
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
  }

}
