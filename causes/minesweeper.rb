def minesweeper width, height, bombs

  board = Array.new(width, 0).map{ Array.new(height, 0) }
  
  while bombs > 0
    x = rand(width)
    y = rand(height)
    if board[x][y] == "*"
      next
    else
      board[x][y] = "*"
      increment_adjacent_spaces x, y, width, height, board
      bombs -= 1
    end
  end
  
  return board
  
end

def increment_adjacent_spaces x, y, width, height, board
  x_range = (x-1)..(x+1)
  y_range = (y-1)..(y+1)
  
  x_range.each do |x_spot|
    y_range.each do |y_spot|
      if x_spot < 0 || y_spot < 0 || x_spot >= width || y_spot >= height || (board[x_spot][y_spot]=="*")
        next
      else
        board[x_spot][y_spot] += 1
      end
    end
  end
  
end
