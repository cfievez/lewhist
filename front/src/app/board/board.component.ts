import { Component, EventEmitter, Inject, Input, OnChanges, OnInit, Output } from '@angular/core';
import { WhistApiDto } from "../config/dto/whist.api.dto";
import { ApiService } from "../config/api.service";
import { ReplaySubject } from "rxjs";
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from "@angular/material/dialog";
import { RxStompService } from "@stomp/ng2-stompjs";
import { CardApiDto } from "../config/dto/card.api.dto";

export type BoardAction = {
  type: GameActionType,
  card?: string,
  bonus?: string,
  value?: number,
  name?: string
}

export type GameActionType = 'TALK' | 'PLAY' | 'PICK_A_WINNER';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.scss']
})
export class BoardComponent implements OnInit {

  @Output()
  action = new EventEmitter<BoardAction>();

  @Input()
  whist: WhistApiDto;

  public contract: number;

  constructor(public dialog: MatDialog) {
  }

  ngOnInit() {
    console.log("init board");
  }

  talk(contract: number) {
    this.action.emit({
      type: "TALK",
      value: contract
    });
  }

  play(card: CardApiDto) {
    if(card.bonusTwo) {
        const dialogRef = this.dialog.open(TwoCardDialog, { data: { card: card.value } as DialogDataCard } );

        dialogRef.afterClosed().subscribe(result => {
          if(result == "distribution") {
            this.playCardWithBonus(card, 'I_DISTRIBUTE');
          } else if(result == "leave") {
            this.playCardWithBonus(card, 'I_LEAVE');
          }
        });
    } else if(card.bonusJoker) {
      const dialogRef = this.dialog.open(JokerCardDialog, { data: { card: card.value } as DialogDataCard } );

      dialogRef.afterClosed().subscribe(result => {
        if(result == "small") {
          this.playCardWithBonus(card, 'I_LEAVE');
        } else if(result == "big") {
          this.playCardWithBonus(card, 'I_TAKE');
        }
      });
    } else {
      this.playCard(card);
    }
  }

  cardIsTwo(card: string): boolean {
    return card.startsWith('2');
  }

  pickAWinner(targetedPlayer: string) {
    this.action.emit({
      type: "PICK_A_WINNER",
      name: targetedPlayer
    });
  }

  private playCardWithBonus(card: CardApiDto, bonus: string) {
    this.action.emit({
      type: "PLAY",
      card: card.value,
      bonus: bonus
    });
  }

  private playCard(card: CardApiDto) {
    this.action.emit({
      type: "PLAY",
      card: card.value
    });
  }
}

@Component({
  selector: 'two-card-dialog',
  templateUrl: 'two-card-dialog.html',
})
export class TwoCardDialog {

  constructor(public dialogRef: MatDialogRef<TwoCardDialog>,
              @Inject(MAT_DIALOG_DATA) public data: DialogDataCard) {
  }

  distribution() {
    this.dialogRef.close('distribution');
  }

  leave() {
    this.dialogRef.close('leave');
  }

}

@Component({
  selector: 'joker-card-dialog',
  templateUrl: 'joker-card-dialog.html',
})
export class JokerCardDialog {

  constructor(public dialogRef: MatDialogRef<TwoCardDialog>,
              @Inject(MAT_DIALOG_DATA) public data: DialogDataCard) {
  }

  big() {
    this.dialogRef.close('big');
  }

  small() {
    this.dialogRef.close('small');
  }

}

export interface DialogDataCard {
  card: string;
}
