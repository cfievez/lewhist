import { Component, OnInit } from '@angular/core';
import { ApiService } from "./config/api.service";
import { Observable, ReplaySubject } from "rxjs";
import { GameViewApiDto } from "./config/dto/gameView.api.dto";
import { BoardAction } from "./board/board.component";
import { RxStompService } from "@stomp/ng2-stompjs";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  title = 'le-whist-front';

  public game$: ReplaySubject<GameViewApiDto> = new ReplaySubject<GameViewApiDto>();

  public usernameInput: string;

  public isAuthenticated: boolean = false;

  public isSpectating: boolean = false;

  public isWaiting: boolean = false;

  public isPlaying: boolean = false;

  constructor(private api: ApiService, private rxStompService: RxStompService, private _snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.loadWhist();
    this.rxStompService.watch('/topic/whist').subscribe((message: any) => {
      if((message.body as string).startsWith('refresh')) {
        this.loadWhist();
      } else {
        this._snackBar.open(message.body, "Fermer", {
          duration: 2000,
        });
      }
    });
  }

  loadWhist(): void {
    this.api.getWhist().subscribe(gameView => {
      console.log(gameView);
      this.game$.next(gameView);
      this.isAuthenticated = gameView.status != 'UNAUTHENTICATED';
      this.isSpectating = gameView.status == 'SPECTATING';
      this.isWaiting = gameView.status == 'WAITING_TO_PLAY';
      this.isPlaying = gameView.status == 'PLAYING';
    });
  }

  connexion(usernameInput: string) {
    this.api.login(usernameInput).subscribe(() => {
      this.loadWhist();
    })
  }

  join() {
    this.api.join().subscribe(() => {
      this.loadWhist();
    })
  }

  start() {
    this.api.start().subscribe(() => {
      this.loadWhist();
    })
  }

  leave() {
    this.api.leave().subscribe(() => {
      this.loadWhist();
    })
  }

  action($boardAction: BoardAction) {
    let apiCall: Observable<any>;
    if($boardAction.type == 'TALK') {
      apiCall = this.api.talk($boardAction.value);
    } else if ($boardAction.type == 'PLAY') {
      if($boardAction.bonus) {
        apiCall = this.api.playWithBonus($boardAction.card, $boardAction.bonus);
      } else {
        apiCall = this.api.play($boardAction.card);
      }
    } else if($boardAction.type == 'PICK_A_WINNER') {
      apiCall = this.api.giveTrickToPlayer($boardAction.name);
    }
    apiCall.subscribe(() => {
      this.loadWhist();
    });
  }

  get canLeave():boolean {
    return this.isPlaying || this.isWaiting;
  }
}
