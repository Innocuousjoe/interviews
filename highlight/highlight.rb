travelers = {}
old_meetings = {}
meetups = []

# Hash modification courtesy of Max Williams on Stack Overflow, 
# http://stackoverflow.com/questions/6227600/how-to-remove-a-key-from-hash-and-get-the-remaining-hash-in-ruby-rails
class Hash
  #pass single or array of keys, which will be removed, returning the remaining hash
  def remove!(*keys)
    keys.each{|key| self.delete(key) }
    self
  end

  #non-destructive version
  def remove(*keys)
    self.dup.remove!(*keys)
  end
end

require 'rubygems'
require 'geocoder'
require 'date'

data = File.open("userdata.txt", "r")



def within_24h? old_person, new_person, new_timestamp, old_meetings
  if old_meetings[old_person]
    if old_meetings[old_person][new_person]
      stamp = old_meetings[old_person][new_person]
      time = DateTime.strptime(new_timestamp, "%s").to_time - DateTime.strptime(stamp, "%s").to_time
      if time/86400 <= 1 #seconds in a day, so within 1 day previous meeting
        return true 
      else
        return false
      end
    end
    return false
  end
end

def within_150m? point_old, point_new
  distance = Geocoder::Calculations.distance_between(point_new, point_old, { :units => :km})*1000
  if distance <= 150
    return true
  end
  return false
end

def within_6h? old_timestamp, new_timestamp
  if (DateTime.strptime(new_timestamp, "%s").to_time - DateTime.strptime(old_timestamp, "%s").to_time)/3600 <= 6 #3600==seconds in an hour
    return true
  else
    return false
  end
end

begin
  while line = data.readline.split("|")
    newbie = line[0]
    new_time = line[1]
    new_lat = line[2]
    new_lon = line[3].gsub("\n", "")
    travelers[newbie] = { "timestamp" => new_time, "lat" => new_lat, "lon" => new_lon } #add user to hash with a nicely formatted sub-hash
    point_new = [new_lat, new_lon] #set up the point for incoming user data
    travelers.remove(newbie).each do |person, values|
      met_or_not = within_24h? person, newbie, new_time, old_meetings
      close_enough_or_not = within_150m? [values["lat"], values["lon"]], point_new
      recent_enough_or_not = within_6h? values["timestamp"], new_time   
      if !met_or_not && close_enough_or_not && recent_enough_or_not
        meetups << new_time + "|" + newbie + "|" + new_lat + "|" + new_lon + "|" + person + "|" + values["lat"] + "|" + values["lon"]
        if old_meetings[newbie]
          old_meetings[newbie][person] = new_time
        else
          old_meetings[newbie] = { person => new_time }
        end

        if old_meetings[person]
          old_meetings[person][newbie] = new_time
        else
          old_meetings[person] = { newbie => new_time }
        end

      end      
    end  
  end
rescue EOFError => e
 File.open("results.txt", 'w') { |file| meetups.each do |meeting| file.puts meeting end }
end


