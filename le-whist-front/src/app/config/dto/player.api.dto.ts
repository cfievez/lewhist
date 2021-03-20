import { CardApiDto } from "./card.api.dto";

export interface PlayerApiDto {

  username: string;

  cards: CardApiDto[];

  cardPlayed: string;

  bonusUsed: string;

  contract: number | null;

  score: number;

  isNextToTalk: boolean;

  isNextToPlay: boolean;

  hasToPickAWinner: boolean;

  isConnectedPlayer: boolean;

  isFirstToPlayThisTrick: boolean;

}
