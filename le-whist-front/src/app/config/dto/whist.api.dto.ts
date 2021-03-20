import { PlayerApiDto } from "./player.api.dto";

export interface WhistApiDto {

  trumpCard: string;

  players: PlayerApiDto[];

  waitingPlayers: string[];

  hasToPickAWinner: boolean;

  gameIsOver: boolean;

}
