/**
 * The MIT License
 * Copyright (c) 2014 Ilkka Seppälä
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import com.iluwatar.event.sourcing.domain.
Account ;importcom.iluwatar.event.sourcing.event.
AccountCreateEvent ;importcom.iluwatar.event.sourcing.event.
MoneyDepositEvent ;importcom.iluwatar.event.sourcing.event.
MoneyTransferEvent ;importcom.iluwatar.event.sourcing.processor.
DomainEventProcessor ;importcom.iluwatar.event.sourcing.state.
AccountAggregate ;importorg.junit.jupiter.api.
BeforeEach ;importorg.junit.jupiter.api.

Test ;importjava.math.
BigDecimal ;importjava.util.

Date ; importstaticcom.iluwatar.event.sourcing.app.App.
ACCOUNT_OF_DAENERYS ; importstaticcom.iluwatar.event.sourcing.app.App.
ACCOUNT_OF_JON ; importstaticorg.junit.jupiter.api.Assertions.

assertEquals
; /**
 * Intergartion Test for Event Sourcing state recovery
 *
 * Created by Serdar Hamzaogullari on 19.08.2017.
 */ public class

  IntegrationTest
  { /**
   * The Domain event processor.
   */ privateDomainEventProcessor

  eventProcessor
  ;/**
   * Initialize.
   */
  @ BeforeEach publicvoidinitialize (
    ) { eventProcessor =newDomainEventProcessor(
  )

  ;
  }/**
   * Test state recovery.
   */
  @ Test publicvoidtestStateRecovery (
    ){eventProcessor.reset(

    );eventProcessor.process (new
        AccountCreateEvent( 0 ,newDate().getTime( ), ACCOUNT_OF_DAENERYS,"Daenerys Targaryen")

    );eventProcessor.process (new
        AccountCreateEvent( 1 ,newDate().getTime( ), ACCOUNT_OF_JON,"Jon Snow")

    );eventProcessor.process (new
        MoneyDepositEvent( 2 ,newDate().getTime( ),  ACCOUNT_OF_DAENERYS ,newBigDecimal("100000"))

    );eventProcessor.process (new
        MoneyDepositEvent( 3 ,newDate().getTime( ),  ACCOUNT_OF_JON ,newBigDecimal("100"))

    );eventProcessor.process (new
        MoneyTransferEvent( 4 ,newDate().getTime( ) ,newBigDecimal("10000" ),
        ACCOUNT_OF_DAENERYS,ACCOUNT_OF_JON)

    ) ; Account accountOfDaenerysBeforeShotDown=AccountAggregate.getAccount(ACCOUNT_OF_DAENERYS
    ) ; Account accountOfJonBeforeShotDown=AccountAggregate.getAccount(ACCOUNT_OF_JON

    );AccountAggregate.resetState(

    ) ; eventProcessor =newDomainEventProcessor(
    );eventProcessor.recover(

    ) ; Account accountOfDaenerysAfterShotDown=AccountAggregate.getAccount(ACCOUNT_OF_DAENERYS
    ) ; Account accountOfJonAfterShotDown=AccountAggregate.getAccount(ACCOUNT_OF_JON

    );assertEquals(accountOfDaenerysBeforeShotDown.getMoney(
        ),accountOfDaenerysAfterShotDown.getMoney()
    );assertEquals(accountOfJonBeforeShotDown.getMoney( ),accountOfJonAfterShotDown.getMoney()
  )

;
