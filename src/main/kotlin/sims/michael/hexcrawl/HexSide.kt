package sims.michael.hexcrawl

enum class HexSide {
    N, NE, SE, S, SW, NW;

    /*
              _ _
            /     \
       _ _ /   N   \ _ _
     /     \       /     \
    /   NW  \ _ _ /  NE   \
    \       /     \       /
     \ _ _ /Origin \ _ _ /
     /     \       /     \
    /   SW  \ _ _ /  SE   \
    \       /     \       /
     \ _ _ /   S   \ _ _ /
           \       /
            \ _ _ /
     */

    companion object {
        fun orderedByFillDirection(fillDirection: FillDirection): List<HexSide> = when (fillDirection) {
            FillDirection.NorthClockwise -> listOf(N, NE, SE, S, SW, NW)
            FillDirection.NorthCounterclockwise -> listOf(N, NW, SW, S, SE, NE)
            FillDirection.SouthClockwise -> listOf(S, SW, NW, N, NE, SE)
            FillDirection.SouthCounterclockwise -> listOf(S, SE, NE, N, NW, SW)
        }
    }
}
