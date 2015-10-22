class Range(start: Long, end: Long){

  def inRange(id: Long): Boolean ={

    //Case where the range is not on the boundary of 0 and MAX
    if(end > start){
      return inRange(start, end, id)
    }
    //Case where range is on the boundary
    else{
      return (inRange(start, Long.MaxValue >>> 1, id) | inRange(0, end, id))
    }

  }

  //Helper function for above.
  private def inRange(start: Long, end: Long, id: Long): Boolean ={
    if(id >= start && id < end){
      return true
    }
    else{
      return false
    }
  }

}