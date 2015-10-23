class Range(start: Long, end: Long){

  def contains(id: Long): Boolean ={

    //Case where the range is not on the boundary of 0 and MAX
    if(end > start){
      return contains(start, end, id)
    }
    //Case where range is on the boundary
    else{
      return (contains(start, Long.MaxValue >>> 1, id) | contains(0, end, id))
    }

  }

  //Helper function for above.
  private def contains(start: Long, end: Long, id: Long): Boolean ={
    if(id >= start && id < end){
      return true
    }
    else{
      return false
    }
  }

  def getStart(): Long ={
    return start
  }

  def getEnd(): Long ={
    return end
  }

  override def toString() : String ={
    return ("Start: " + start + " End: " + end)
  }

}